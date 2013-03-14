import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: mthorpe
 * Date: 13/03/2013
 * Time: 22:19
 */
public class SFDProcess extends Process {
    private IFailureDetector detector;

    // Map PID to received
    Lock l;
    Map<Integer, AtomicInteger> signals;

    public SFDProcess(String name, int pid, int n, String x) {
        super(name,pid,n);
        detector = new StrongFailureDetector(this);
        signals = new HashMap<>(this.getNo());
        l = new ReentrantLock();
    }

    public void begin() {
        detector.begin();
        for (int i = 0; i < getNo(); i++ ) {
            if (i == pid) {
                // send [VAL: x, r] to all processes in G
                // we send i implicitly since,
                this.broadcast("consensus", Integer.toString(detector.getLeader()));
            }
        }
    }

    public synchronized void receive (Message m) {
        String type = m.getType();
        if (type.equals("heartbeat")) {
            detector.receive(m);
        } else if (type.equals("consensus")) {
            // lock the signals map
            l.lock();
            signals.put(m.getSource(), new AtomicInteger(Integer.parseInt(m.getPayload())));
            l.unlock();
        }
    }

    public static void main (String[] args) {
        SFDProcess p = new SFDProcess("P1", 1, 2, args[3]);
        p.registeR();
        p.begin();
    }


}
