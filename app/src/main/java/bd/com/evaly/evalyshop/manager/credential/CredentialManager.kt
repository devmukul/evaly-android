package bd.com.evaly.evalyshop.manager.credential

import android.app.Activity
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import bd.com.evaly.evalyshop.util.Constants
import bd.com.evaly.evalyshop.util.Constants.RC_READ
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import javax.inject.Inject


class CredentialManager @Inject constructor(
    val credentialClient: CredentialsClient,
    val credentialRequest: CredentialRequest,
    val activity: Activity
) {

    fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Credentials.getClient(activity).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            activity,
            intent.intentSender,
            Constants.PHONE_PICKER_REQUEST,
            null, 0, 0, 0, null
        )
    }

    fun saveCredential(
        phone: String,
        password: String,
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
                    // This will prompt the user if the credential is new.
                    rae.startResolutionForResult(activity, Constants.RC_SAVE)
                } catch (exception: SendIntentException) {
                    saveListener.onCredentialSaveError()
                }
            } else {
                saveListener.onCredentialSaveError()
            }
        }
    }

    fun retrieveUserCredential(fetchListener: CredentialFetchListener) {
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
                        resolveResult(e, RC_READ)
                    }
                }
            }

    }

    private fun resolveResult(rae: ResolvableApiException, requestCode: Int) {
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