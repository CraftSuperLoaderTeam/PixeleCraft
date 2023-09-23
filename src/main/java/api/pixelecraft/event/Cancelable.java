package api.pixelecraft.event;

public interface Cancelable {
    boolean isCancel();
    void setCancel(boolean cancel);
}
