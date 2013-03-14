import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: mthorpe
 * Date: 14/03/2013
 * Time: 14:26
 */
public class ESFDProcess extends SFDProcess {

    int messagesReceived;
    AtomicInteger count;

    public ESFDProcess(String name, int pid, int n) {
        super(name, pid, n);
        messagesReceived = 0;
        count = new AtomicInteger(getNo() - (getNo()/3));
    }

    @Override
    public void begin() {

        AtomicInteger r = new AtomicInteger(0);
        int m = 0;

        while (true) {
            r = new AtomicInteger(r.getAndIncrement());
            int c = (r.get() % getNo()) + 1;

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
        if (type.equals("heartbeat")) {
            detector.receive(m);
        } else if (type.equals("consensus")) {
            count = new AtomicInteger(count.getAndIncrement());
            Object signal = signals.get(m.getSource());
            synchronized(signal) {
                values.put(m.getSource(), Integer.parseInt(m.getPayload()));
                signal.notifyAll();
            }
        } else if (type.equals("outcome")) {

        }

    }
}
