import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.*;

public class CoordSelectorItem extends Item {
    private Map<String, BlockPos> parent = new HashMap<>();
    private BlockPos start, end;
    
    public CoordSelectorItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockHitResult result = context.getHitResult();
        BlockPos pos = result.getBlockPos();
        
        if (start == null) {
            start = pos;
        } else if (end == null) {
            end = pos;
            findPath(world, start, end);
        } else {
            start = end = null;
            parent.clear();
        }
        
        return ActionResult.SUCCESS;
    }
    
    private void findPath(World world, BlockPos start, BlockPos end) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.offer(start);
        parent.put(start.toString(), null);
        
        while (!queue.isEmpty()) {
            BlockPos curr = queue.poll();
            if (curr.equals(end)) {
                constructPath(parent, start, end);
                return;
            }
            
            for (Direction direction : Direction.values()) {
                BlockPos next = curr.offset(direction);
                if (!parent.containsKey(next.toString()) && world.getBlockState(next).isAir()) {
                    parent.put(next.toString(), curr);
                    queue.offer(next);
                }
            }
        }
    }
    
    private List<BlockPos> constructPath(Map<String, BlockPos> parent, BlockPos start, BlockPos end) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos curr = end;
        while (curr != null) {
            path.add(curr);
            curr = parent.get(curr.toString());
        }
        Collections.reverse(path);
        
        for (BlockPos pos : path) {
            world.setBlockState(pos, DIAMOND_BLOCK.getDefaultState(), 2);
        }
        
        return path;
    }
}
