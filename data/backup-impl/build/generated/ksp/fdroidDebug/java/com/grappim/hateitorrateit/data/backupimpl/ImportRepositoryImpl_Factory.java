package com.grappim.hateitorrateit.data.backupimpl;

import android.content.Context;
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage;
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository;
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.serialization.json.Json;

@ScopeMetadata
@QualifierMetadata({
    "dagger.hilt.android.qualifiers.ApplicationContext",
    "com.grappim.hateitorrateit.core.async.IoDispatcher"
})
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ImportRepositoryImpl_Factory implements Factory<ImportRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<ProductsRepository> productsRepositoryProvider;

  private final Provider<LocalDataStorage> localDataStorageProvider;

  private final Provider<FolderPathManager> folderPathManagerProvider;

  private final Provider<Json> jsonProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private ImportRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<ProductsRepository> productsRepositoryProvider,
      Provider<LocalDataStorage> localDataStorageProvider,
      Provider<FolderPathManager> folderPathManagerProvider, Provider<Json> jsonProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.contextProvider = contextProvider;
    this.productsRepositoryProvider = productsRepositoryProvider;
    this.localDataStorageProvider = localDataStorageProvider;
    this.folderPathManagerProvider = folderPathManagerProvider;
    this.jsonProvider = jsonProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ImportRepositoryImpl get() {
    return newInstance(contextProvider.get(), productsRepositoryProvider.get(), localDataStorageProvider.get(), folderPathManagerProvider.get(), jsonProvider.get(), ioDispatcherProvider.get());
  }

  public static ImportRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<ProductsRepository> productsRepositoryProvider,
      Provider<LocalDataStorage> localDataStorageProvider,
      Provider<FolderPathManager> folderPathManagerProvider, Provider<Json> jsonProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ImportRepositoryImpl_Factory(contextProvider, productsRepositoryProvider, localDataStorageProvider, folderPathManagerProvider, jsonProvider, ioDispatcherProvider);
  }

  public static ImportRepositoryImpl newInstance(Context context,
      ProductsRepository productsRepository, LocalDataStorage localDataStorage,
      FolderPathManager folderPathManager, Json json, CoroutineDispatcher ioDispatcher) {
    return new ImportRepositoryImpl(context, productsRepository, localDataStorage, folderPathManager, json, ioDispatcher);
  }
}
