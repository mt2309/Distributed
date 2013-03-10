import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 15:32
 */
public class PerfectFailureDetector implements IFailureDetector {

    Process p;
    LinkedList<Integer> suspects;
    Timer t;

    static final int SECOND = 1000;

    final class PeriodicTask extends TimerTask {
        public void run() {
            p.broadcast("heartbeat","null");
        }
    }

    public PerfectFailureDetector(Process p) {
        this.p = p;
        this.t = new Timer();
        suspects = new LinkedList<>();
    }

    @Override
    public void begin() {
        t.schedule(new PeriodicTask(),0, SECOND);
    }

    @Override
    public void receive(Message m) {
        Utils.out(p.pid,m.toString());
    }

    @Override
    public boolean isSuspect(Integer process) {
        return suspects.contains(process);
    }

    @Override
    public int getLeader() {
        return -1;
    }

    @Override
    public void isSuspected(Integer process) {
        
    }
}
