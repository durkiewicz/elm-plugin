package org.elmlang.intellijplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.elmlang.intellijplugin.psi.ElmFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class ElmModuleIndex extends ScalarIndexExtension<String> {
    private static final ID<String, Void> ELM_MODULE_INDEX = ID.create("ElmModuleIndex");
    private static final String PLATFORM_PREFIX = "Platform.";

    private static final EnumeratorStringDescriptor KEY_DESCRIPTOR = new EnumeratorStringDescriptor();

    private static final DataIndexer<String, Void, FileContent> INDEXER = inputData -> {
        final PsiFile psiFile = inputData.getPsiFile();
        final String moduleName = psiFile instanceof ElmFile ? ((ElmFile) psiFile).getModuleName() : null;
        if (moduleName == null) {
            return Collections.emptyMap();
        } else if (moduleName.startsWith(PLATFORM_PREFIX)) {
            return new HashMap<String, Void>() {{
                put(moduleName, null);
                put(moduleName.substring(PLATFORM_PREFIX.length()), null);
            }};
        }
        return Collections.singletonMap(moduleName, null);
    };

    private static final FileBasedIndex.InputFilter ELM_MODULE_FILTER =
            file -> file.getFileType() == ElmFileType.INSTANCE && file.isInLocalFileSystem();

    @NotNull
    public static List<ElmFile> getFilesByModuleName(String moduleName, Project project) {
        return getFilesByModuleName(moduleName, project, GlobalSearchScope.projectScope(project));
    }

    public static Collection<String> getAllModuleNames(Project project) {
        return FileBasedIndex.getInstance().getAllKeys(ELM_MODULE_INDEX, project);
    }

    @NotNull
    private static List<ElmFile> getFilesByModuleName(@NotNull String moduleName, @NotNull Project project, @NotNull GlobalSearchScope searchScope) {
        final PsiManager psiManager = PsiManager.getInstance(project);
        Collection<VirtualFile> virtualFiles = getVirtualFilesByModuleName(moduleName, searchScope);
        return ContainerUtil.mapNotNull(virtualFiles, virtualFile -> {
            final PsiFile psiFile = psiManager.findFile(virtualFile);
            return psiFile instanceof ElmFile ? (ElmFile)psiFile : null;
        });
    }

    @NotNull
    private static Collection<VirtualFile> getVirtualFilesByModuleName(@NotNull String moduleName, @NotNull GlobalSearchScope searchScope) {
        return FileBasedIndex.getInstance().getContainingFiles(ELM_MODULE_INDEX, moduleName, searchScope);
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return ELM_MODULE_INDEX;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return INDEXER;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return KEY_DESCRIPTOR;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return ELM_MODULE_FILTER;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 2;
    }
}