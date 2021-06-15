package bd.com.evaly.evalyshop.manager.credential

import com.google.android.gms.auth.api.credentials.Credential

interface CredentialFetchListener {
    fun onSingleCredentialFetchSuccess(credential: Credential)
}