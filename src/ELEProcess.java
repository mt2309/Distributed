/**
 * User: mthorpe
 * Date: 12/03/2013
 * Time: 22:31
 */
public class ELEProcess extends Process {

    private IFailureDetector detector;

    public ELEProcess(String name, int pid, int n) {
        super(name, pid, n);
        detector = new EventuallyLeaderElector(this);
    }

    public void begin() {
        detector.begin();
    }

    @Override
    public synchronized void receive(Message m) {
        String type = m.getType();
        if (type.equals("heartbeat")) {
            detector.receive(m);
        }
    }

    public static void main(String[] args) {
        ELEProcess p = new ELEProcess("P1", 1, 2);
        p.registeR();
        p.begin();
    }
}
