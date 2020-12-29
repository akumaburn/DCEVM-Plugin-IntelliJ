package icons;

import com.eonmux.dcevm.Attributes;
import com.esotericsoftware.minlog.Log;
import com.intellij.ui.IconManager;
import com.intellij.util.ReflectionUtil;
import kotlin.Pair;

import javax.swing.*;

/**
 * @author Amir Eslampanah
 * @date date(" EEEEE yyyy - MM - dd HH : mm : ssZ ")
 */
public class PluginIcons {

  public static final Icon RELOAD_13 = load("../reload_13.png");
  public static final Icon RELOAD_16 = load("../reload_16.png");
  public static final Icon RELOAD_24 = load("../reload_24.png");
  public static final Icon RELOAD_32 = load("../reload_32.png");
  public static final Icon RELOAD_64 = load("../reload_64.png");

  public static final Icon RELOAD_OFF_13 = load("../reload_off_13.png");
  public static final Icon RELOAD_OFF_16 = load("../reload_off_16.png");
  public static final Icon RELOAD_OFF_24 = load("../reload_off_24.png");
  public static final Icon RELOAD_OFF_32 = load("../reload_off_32.png");
  public static final Icon RELOAD_OFF_64 = load("../reload_off_64.png");

  private static Icon load(String path) {
    return IconManager.getInstance().getIcon(path, PluginIcons.class);
  }
}
