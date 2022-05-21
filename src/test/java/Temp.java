import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.ArmorSetsData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import org.easymock.EasyMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class Temp extends AbstractTest {
	@Mock
	private Autobot bot;
	private static final String DATAPACK = "-dp";

	private static final String SCRIPT = "-s";

	private static final String GEODATA = "-gd";
	@BeforeTest
	private void initConfig(){
		String [] args = {
				"-dp",
				"C:\\Users\\Silvar\\Desktop\\l2workspace\\l2j-server-datapack-angel\\src\\main\\resources" ,
				"-s",
				"C:\\Users\\Silvar\\Desktop\\l2workspace\\l2j-server-datapack-angel\\src\\main\\java",
				"-gd",
				"C:\\Users\\Silvar\\Desktop\\l2workspace\\l2j-server-datapack-angel\\src\\main\\resources\\data\\geodata"};
		final String datapackRoot = com.l2jserver.commons.util.Util.parseArg(args, DATAPACK, true);
		if (datapackRoot != null) {
			Configuration.server().setProperty("DatapackRoot", datapackRoot);
		}

		final String scriptRoot = com.l2jserver.commons.util.Util.parseArg(args, SCRIPT, true);
		if (scriptRoot != null) {
			Configuration.server().setProperty("ScriptRoot", scriptRoot);
		}

		final String geodata = com.l2jserver.commons.util.Util.parseArg(args, GEODATA, true);
		if (geodata != null) {
			Configuration.geodata().setProperty("GeoDataPath", geodata);
		}
		ConnectionFactory.builder() //
				.withDriver(Configuration.database().getDriver()) //
				.withUrl(Configuration.database().getURL()) //
				.withUser(Configuration.database().getUser()) //
				.withPassword(Configuration.database().getPassword()) //
				.withConnectionPool(Configuration.database().getConnectionPool()) //
				.withMaxIdleTime(Configuration.database().getMaxIdleTime()) //
				.withMaxPoolSize(Configuration.database().getMaxConnections()) //
				.build();

		DAOFactory.getInstance();
	}
	@AfterTest
	void afterTest(){
		ConnectionFactory.getInstance().close();
	}
	@Test
	public void testEventEngine() {
		ItemTable.getInstance();
		ArmorSetsData.getInstance();
		PcInventory inv = new PcInventory(bot);
		EasyMock.expect(bot.getLevel()).andStubReturn(76);
		EasyMock.expect(bot.getClassId()).andStubReturn(ClassId.duelist);
		EasyMock.expect(bot.getInventory()).andStubReturn(inv);
		EasyMock.expect(bot.isGM()).andStubReturn(false);
		EasyMock.expect(bot.getObjectId()).andDelegateTo(1);
		EasyMock.replay(bot);
		AutobotHelpers.giveItemsByGrade(bot,false);
	}

}
