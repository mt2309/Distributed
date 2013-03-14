import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: mthorpe
 * Date: 14/03/2013
 * Time: 14:26
 */
public class ESFDProcess extends SFDProcess {

    int messagesReceived;
    int count;
	
	Map<Integer, Integer> d_values;
	Map<Integer, Integer> v_values;

    public ESFDProcess(String name, int pid, int n) {
        super(name, pid, n);
        messagesReceived = 0;
        count = (getNo() - (getNo()/3));
    }

    @Override
    public void begin() {

        int r = 0;
        int m = 0;

        while (true) {
            r++;
            int c = (r % getNo()) + 1;

            Message msg = new Message(pid,c,"consensus",Integer.toString(detector.getLeader()));
            unicast(msg);

            if (c == pid) {
                int v = 0;
                broadcast("outcome",Integer.toString(v));
            }
        }
    }

    @Override
    public synchronized void receive(Message m) {
        String type = m.getType();
        switch (type) {
            case "heartbeat":
                detector.receive(m);
                break;
            case "consensus":
				count++;
                values.put(m.getSource(), Integer.parseInt(m.getPayload()));
                break;
            case "outcome":
                Object signal = signals.get(m.getSource());
                synchronized (signal) {
					string[] vals = string.split(" ", m.getPayload());
					d_values.put(m.getSource(), Integer.parseInt(vals[0]));
					v_values.put(m.getSource(), Integer.parseInt(vals[1]));
                    signal.notifyAll();
                }

                break;
        }

    }
}
