package com.grappim.hateitorrateit.data.backupimpl.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class BackupModule_Companion_ProvideJsonFactory implements Factory<Json> {
  @Override
  public Json get() {
    return provideJson();
  }

  public static BackupModule_Companion_ProvideJsonFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Json provideJson() {
    return Preconditions.checkNotNullFromProvides(BackupModule.Companion.provideJson());
  }

  private static final class InstanceHolder {
    static final BackupModule_Companion_ProvideJsonFactory INSTANCE = new BackupModule_Companion_ProvideJsonFactory();
  }
}
