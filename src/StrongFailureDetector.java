/**
 * User: mthorpe
 * Date: 13/03/2013
 * Time: 21:58
 */
public class StrongFailureDetector extends EventuallyPerfectFailureDetector {

    String x;

    public StrongFailureDetector(Process p, String x) {
        super(p);
        this.x = x;
    }

    @Override
    public void receive(Message m) {
        // If its a heartbeat message, just handle it like any other
        if (m.getType().equals("heartbeat"))
            super.receive(m);

        else if (m.getType().equalsIgnoreCase("consensus")) {
            // consensus check message
            p.broadcast("consensus",Integer.toString(getLeader()));

        }
    }

    @Override
    public int getLeader() {
        return super.getLeader();
    }

    @Override
    public boolean isSuspect(Integer process) {
        return super.isSuspect(process);
    }

    @Override
    public void isSuspected(Integer process) {
        super.isSuspected(process);
    }
}
