import com.l2jserver.datapack.eventengine.enums.ScoreType;
import com.l2jserver.datapack.eventengine.interfaces.IParticipant;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.model.holders.Participant;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Temp extends AbstractTest {

	@Test
	public void testEventEngine() {
		Set<IParticipant> p1 = new HashSet<>();
		p1.add(new Player(1));
		p1.add(new Player(2));

		List<Participant> p2 = p1.stream().map(p->new Participant("was"+new Random().nextInt(2), new Random().nextInt(10))).sorted(Comparator.comparingInt(Participant::getPoints)).collect(Collectors.toList());
		System.out.println(p2);
	}
}
