import java.util.HashMap;
import java.util.Map;

/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 21:57
 */
public class EventuallyPerfectFailureDetector extends AbstractFailureDetector {

    Map<Integer,Long> lastSeen;

    public EventuallyPerfectFailureDetector(Process p) {
        super(p);
        lastSeen = new HashMap<>(p.getNo());
    }

    @Override
    public void receive(Message m) {
        super.receive(m);
        int source = m.getSource();
        long delay = (System.currentTimeMillis() - Long.parseLong(m.getPayload()));

        if (delay > lastSeen.get(source)) {
            lastSeen.put(source, delay);
        }
    }

    @Override
    int delay(int i) {
        return lastSeen.get(i).intValue();
    }
}
