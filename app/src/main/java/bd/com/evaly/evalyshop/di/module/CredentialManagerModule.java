package bd.com.evaly.evalyshop.di.module;

import android.app.Activity;
import android.content.Context;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;

import bd.com.evaly.evalyshop.manager.credential.CredentialManager;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;

@Module
@InstallIn(ActivityComponent.class)
public class CredentialManagerModule {

    @Provides
    CredentialRequest credentialRequest() {
        return new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();
    }

    @Provides
    CredentialsOptions provideCredentialOption() {
        return new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
    }

    @Provides
    CredentialsClient provideCredentialClient(Context context, CredentialsOptions options) {
        return Credentials.getClient(context, options);
    }

    @Provides
    CredentialManager provideCredentialManager(
            CredentialsClient credentialClient,
            CredentialRequest credentialRequest,
            Activity activity ) {
        return new CredentialManager(credentialClient, credentialRequest, activity);
    }
}
