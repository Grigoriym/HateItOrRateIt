package com.grappim.hateitorrateit.data.backupimpl;

import android.content.Context;
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider;
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage;
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository;
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils;
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
public final class BackupRepositoryImpl_Factory implements Factory<BackupRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<ProductsRepository> productsRepositoryProvider;

  private final Provider<LocalDataStorage> localDataStorageProvider;

  private final Provider<AppInfoProvider> appInfoProvider;

  private final Provider<FolderPathManager> folderPathManagerProvider;

  private final Provider<Json> jsonProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private final Provider<DateTimeUtils> dateTimeUtilsProvider;

  private BackupRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<ProductsRepository> productsRepositoryProvider,
      Provider<LocalDataStorage> localDataStorageProvider,
      Provider<AppInfoProvider> appInfoProvider,
      Provider<FolderPathManager> folderPathManagerProvider, Provider<Json> jsonProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<DateTimeUtils> dateTimeUtilsProvider) {
    this.contextProvider = contextProvider;
    this.productsRepositoryProvider = productsRepositoryProvider;
    this.localDataStorageProvider = localDataStorageProvider;
    this.appInfoProvider = appInfoProvider;
    this.folderPathManagerProvider = folderPathManagerProvider;
    this.jsonProvider = jsonProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
    this.dateTimeUtilsProvider = dateTimeUtilsProvider;
  }

  @Override
  public BackupRepositoryImpl get() {
    return newInstance(contextProvider.get(), productsRepositoryProvider.get(), localDataStorageProvider.get(), appInfoProvider.get(), folderPathManagerProvider.get(), jsonProvider.get(), ioDispatcherProvider.get(), dateTimeUtilsProvider.get());
  }

  public static BackupRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<ProductsRepository> productsRepositoryProvider,
      Provider<LocalDataStorage> localDataStorageProvider,
      Provider<AppInfoProvider> appInfoProvider,
      Provider<FolderPathManager> folderPathManagerProvider, Provider<Json> jsonProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<DateTimeUtils> dateTimeUtilsProvider) {
    return new BackupRepositoryImpl_Factory(contextProvider, productsRepositoryProvider, localDataStorageProvider, appInfoProvider, folderPathManagerProvider, jsonProvider, ioDispatcherProvider, dateTimeUtilsProvider);
  }

  public static BackupRepositoryImpl newInstance(Context context,
      ProductsRepository productsRepository, LocalDataStorage localDataStorage,
      AppInfoProvider appInfoProvider, FolderPathManager folderPathManager, Json json,
      CoroutineDispatcher ioDispatcher, DateTimeUtils dateTimeUtils) {
    return new BackupRepositoryImpl(context, productsRepository, localDataStorage, appInfoProvider, folderPathManager, json, ioDispatcher, dateTimeUtils);
  }
}
