import java.util.HashSet;
import java.util.Set;

/**
 * User: mthorpe
 * Date: 12/03/2013
 * Time: 20:55
 */
public class LeaderDetector extends EventuallyPerfectFailureDetector {


    int leader;

    LeaderDetector(Process process) {
        super(process);
        // leader starts off as current process
        leader = process.getPid();
    }

    @Override
    public void receive(Message m) {
        int size = suspects.size();
        super.receive(m);
        if (size != suspects.size()) {
            updateLeader();
        }
    }

    void updateLeader() {
        Set<Integer> proc = new HashSet<>(processes);
        proc.removeAll(suspects);
        for (Integer p : proc) {
            if (leader < p)
                leader = p;
        }
    }

    @Override
    public int getLeader() {
        return leader;
    }
}
