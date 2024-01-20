package org.jetbrains.idea.perforce;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.VcsConfiguration;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.perforce.perforce.PerforceSettings;
import org.jetbrains.idea.perforce.perforce.connections.AbstractP4Connection;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PerforceDvcsTest extends PerforceTestCase {

  @Override
  public void before() throws Exception {
    super.before();
    enableSilentOperation(VcsConfiguration.StandardConfirmation.ADD);
  }

  @Override
  protected void setupWorkspace() {
    PerforceSettings.getSettings(myProject).useP4CONFIG = true;
    AbstractP4Connection.setTestEnvironment(Map.of("PATH", myClientBinaryPath.getPath()), myTestRootDisposable);

    ensureNoEnvP4Config();

    verify(runP4(new String[] { "configure", "set", "server.allowfetch=3" }, null));
    verify(runP4(new String[] { "configure", "set", "server.allowpush=3" }, null));

    final boolean caseInsensitive = SystemInfo.isWindows;
    verify(runP4Bare(new String[] { "init", "-C" + (caseInsensitive ? "1" : "0") }, null));
    verify(runP4Bare(new String[] { "info" }, null));
  }

  @Override
  protected void submitDefaultList(String desc) {
    verify(runP4Bare(new String[]{"submit", "-d", desc}, null));
    refreshVfs();
    getChangeListManager().waitUntilRefreshed();
  }

  @Test
  public void testLocalChanges() {
    ensureNoP4Ignore();
    final VirtualFile file = createFileInCommand("a.txt", "a");
    getChangeListManager().waitUntilRefreshed();

    assertEquals(file, getSingleChange().getVirtualFile());

    submitDefaultList("initial");
    assertChangesViewEmpty();

    editFileInCommand(file, "b");
    getChangeListManager().waitUntilRefreshed();
    getChangeListManager().waitUntilRefreshed();

    assertEquals(file, getSingleChange().getVirtualFile());
  }

  private void ensureNoP4Ignore() {
    if (myP4IgnoreFile.exists()) {
      FileUtil.delete(myP4IgnoreFile);
    }
    refreshChanges();
  }
}
