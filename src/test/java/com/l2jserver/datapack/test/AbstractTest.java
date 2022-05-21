/*
 * Copyright © 2004-2021 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.test;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Abstract Test.
 *
 * @author Zoey76
 * @version 2.6.2.0
 */
@PrepareForTest({
        L2PcInstance.class,
        Skill.class
})
@PowerMockIgnore({
        "javax.xml.*",
        "org.w3c.*",
        "org.apache.*",
        "org.slf4j.*",
        "com.sun.*",
        "javax.management.*",
        "org.xml.sax.ErrorHandler",
        "com.l2jserver.gameserver.util.IXmlReader$XMLErrorHandler"
})
public class AbstractTest extends PowerMockTestCase {

}
