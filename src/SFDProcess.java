/**
 * User: mthorpe
 * Date: 13/03/2013
 * Time: 22:19
 */
public class SFDProcess extends Process {
    private IFailureDetector detector;

    public SFDProcess(String name, int pid, int n, String x) {
        super(name,pid,n);
        detector = new StrongFailureDetector(this ,x);
    }

    public void begin() {
        detector.begin();
    }

    public synchronized void receive (Message m) {
        String type = m.getType();
        if (type.equals("heartbeat")) {
            detector.receive(m);
        }
    }

    public static void main (String[] args) {
        SFDProcess p = new SFDProcess("P1", 1, 2, args[3]);
        p.registeR();
        p.begin();
    }


}
