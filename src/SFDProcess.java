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
    Map<Integer, Object> signals;
	Map<Integer, Integer> values;

    public SFDProcess(String name, int pid, int n, String x) {
        super(name,pid,n);
        detector = new StrongFailureDetector(this);
        signals = new HashMap<>(this.getNo());
        values = new HashMap<>(this.getNo());

        for (int i = 0; i < getNo(); i++ ) {
			signals.put(i, new Object());
			values.put(i, -1);
		}
    }

	private boolean collect(int r) {
        try {
			Object signal = signals.get(r);
			synchronized(signal) {
				signal.wait();
			}
            return !(values.get(r) == -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
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
			values.put(m.getSource(), Integer.parseInt(m.getPayload()));
			Object signal = signals.get(m.getSource());
			synchronized(signal) {
				signal.notifyAll();
			}
        }
    }

    public static void main (String[] args) {
        SFDProcess p = new SFDProcess("P1", 1, 2, args[3]);
        p.registeR();
        p.begin();
    }


}
