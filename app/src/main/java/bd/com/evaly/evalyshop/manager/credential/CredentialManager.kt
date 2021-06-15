package bd.com.evaly.evalyshop.manager.credential

import android.app.Activity
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.util.Log
import bd.com.evaly.evalyshop.util.Constants
import bd.com.evaly.evalyshop.util.Constants.RC_READ
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import javax.inject.Inject


class CredentialManager @Inject constructor(
    val credentialClient: CredentialsClient,
    val credentialRequest: CredentialRequest,
) {

    fun saveCredential(
        phone: String,
        password: String,
        activity: Activity,
        saveListener: CredentialSaveListener
    ) {
        val credential = getCredentialInstance(phone, password)

        credentialClient.save(credential).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                Log.d("SignInActivity:", "SAVE: OK")
                saveListener.onCredentialSave()
                return@addOnCompleteListener
            }
            val e = task.exception
            if (e is ResolvableApiException) {
                val rae = e as ResolvableApiException
                try {
                    rae.startResolutionForResult(activity, Constants.RC_SAVE)
                } catch (exception: SendIntentException) {
                    saveListener.onCredentialSaveError()
                }
            } else {
                saveListener.onCredentialSaveError()
            }
        }
    }

    fun retrieveUserCredential(activity: Activity, fetchListener: CredentialFetchListener) {
        credentialClient.request(credentialRequest)
            .addOnCompleteListener { task: Task<CredentialRequestResponse> ->
                if (task.isSuccessful) {
                    task.result.credential?.let {
                        fetchListener.onSingleCredentialFetchSuccess(it)
                        return@addOnCompleteListener
                    }
                } else {
                    Log.e("credentialName", "Faield To Retrieve")
                    val e = task.exception
                    if (e is ResolvableApiException) {
                        // May be Multiple user saved, now show user picker..
                        resolveResult(e, RC_READ, activity)
                    }
                }
            }

    }

    private fun resolveResult(rae: ResolvableApiException, requestCode: Int, activity: Activity) {
        try {
            rae.startResolutionForResult(activity, requestCode)
        } catch (exp: IntentSender.SendIntentException) {
            //Log.e(TAG, "Failed to send resolution.", e);
        }
    }

    private fun getCredentialInstance(phone: String, password: String): Credential =
        Credential.Builder(phone)
            .setPassword(password)
            .setName(phone)
            .build()

}