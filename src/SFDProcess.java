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
    protected IFailureDetector detector;

    // Map PID to received
    Map<Integer, Object> signals;
	Map<Integer, Integer> values;

    public SFDProcess(String name, int pid, int n) {
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
				while(!detector.isSuspect(r) && values.get(r) == -1) {
					signal.wait(1000);
                }
			}

            return !detector.isSuspect(r);
		}
        catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
	}

    public void begin() {
        detector.begin();
		int x = detector.getLeader();
        for (int i = 0; i < getNo(); i++ ) {
            if (i == pid) {
                // send [VAL: x, r] to all processes in G
                // we send i implicitly since,
                this.broadcast("consensus", Integer.toString(detector.getLeader()));
            }
			if(collect(i))
			{
				Object signal = signals.get(m.getSource());
				synchronized(signal) {
					x = values.get(i);
				}
			}
        }
		
        Utils.out(pid, String.format("Decided on %d.", x));
    }

    public synchronized void receive (Message m) {
        String type = m.getType();
        if (type.equals("heartbeat")) {
            detector.receive(m);
        } else if (type.equals("consensus")) {
			Object signal = signals.get(m.getSource());
			synchronized(signal) {
				values.put(m.getSource(), Integer.parseInt(m.getPayload()));
				signal.notifyAll();
			}
        }
    }

    public static void main (String[] args) {
        SFDProcess p = new SFDProcess("P1", 1, 2);
        p.registeR();
        p.begin();
    }


}
