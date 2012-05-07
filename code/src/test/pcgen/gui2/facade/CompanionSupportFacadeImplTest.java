/*
 * CompanionSupportFacadeImplTest.java
 * Copyright James Dempsey, 2012
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 06/04/2012 8:30:30 AM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import org.junit.Test;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.DataSet;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.system.LoadInfo;
import pcgen.util.TestHelper;

/**
 * The Class <code></code> ...
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CompanionSupportFacadeImplTest extends AbstractCharacterTestCase
{

	private MockUIDelegate uiDelegate;
	private DataSetFacade dataSetFacade;
	private Race masterRace;
	private Race companionRace;
	private CompanionList companionList;
	private TodoManager todoManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
		ListFacade<CampaignFacade> campaigns = new DefaultListFacade<CampaignFacade>();
		dataSetFacade = new DataSet(Globals.getContext(), SettingsHandler.getGame(), campaigns );
		masterRace = TestHelper.makeRace("Wood Elf");
		companionRace = TestHelper.makeRace("Weasel");

		CDOMReference<Race> race  = new  CDOMDirectSingleRef<Race>(companionRace);
		CDOMSingleRef<CompanionList> ref  = new  CDOMSimpleSingleRef<CompanionList>(CompanionList.class, companionList.getKeyName());
		FollowerOption option = new FollowerOption(race, ref);
		masterRace.addToListFor(ListKey.COMPANIONLIST, option);
	}

	/**
	 * Test method for {@link pcgen.gui2.facade.CompanionSupportFacadeImpl#addCompanion(pcgen.core.facade.CharacterFacade, java.lang.String)}.
	 */
	@Test
	public void testAddCompanion()
	{
		PlayerCharacter master = getCharacter();
		master.setRace(masterRace);
		master.setFileName("Master.pcg");
		master.setName("Master1");
		CompanionSupportFacadeImpl masterCsfi = new CompanionSupportFacadeImpl(master, todoManager);
		
		PlayerCharacter companion = new PlayerCharacter();
		companion.setRace(companionRace);
		companion.setFileName("Companion.pcg");
		companion.setName("Companion1");
		CharacterFacadeImpl compFacade = new CharacterFacadeImpl(companion, uiDelegate, dataSetFacade);

		assertNull("No companion type should be set yet.", compFacade.getCompanionType());
		assertTrue("Master should have no companions", master.getFollowerList().isEmpty());
		
		masterCsfi.addCompanion(compFacade, "Familiar");
		Follower follower = master.getFollowerList().iterator().next();
		assertEquals("Companion should be the first follower", companion.getName(), follower.getName());
		assertEquals("Master should have one companion", 1, master.getFollowerList().size());
		
		assertNotNull("Companion should have a master now", companion.getMaster());
		assertEquals("Companion's master", master.getName(), companion.getMaster().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void additionalSetUp() throws Exception
	{
		companionList = Globals.getContext().ref.constructNowIfNecessary(CompanionList.class,
			"Familiar");
	}

}
