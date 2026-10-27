package dk.xenos.computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public final class ContractSelector {
    private ContractSelector() { }

    public static List<String> pick(UUID player, String cycle, boolean weekly,
                                    List<String> available, int size) {
        if (player == null || cycle == null || available == null || size < 0) {
            throw new IllegalArgumentException("Invalid rotation arguments");
        }
        List<String> shuffled = new ArrayList<String>(available);
        long seed = ((long) player.hashCode() << 32) ^ cycle.hashCode()
                ^ (weekly ? 0xF82AC1L : 0xAB172DL);
        Collections.shuffle(shuffled, new Random(seed));
        return new ArrayList<String>(shuffled.subList(0, Math.min(size, shuffled.size())));
    }
}
