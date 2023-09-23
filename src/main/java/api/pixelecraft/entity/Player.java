package api.pixelecraft.entity;

public interface Player extends HumanEntity{
    String getDisplayName();
    String getName();
    boolean isOp();
}
