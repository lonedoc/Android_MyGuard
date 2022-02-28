package kobramob.rubeg38.ru.myprotection.feature.accounts.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.Router
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentPaymentPageBinding
import kobramob.rubeg38.ru.myprotection.domain.models.Account
import org.koin.android.ext.android.inject

private const val MIME_TYPE = "text/html"
private const val ENCODING = "utf-8"

class PaymentPageFragment : Fragment(R.layout.fragment_payment_page) {

    companion object {
        private const val ACCOUNT_KEY = "account"
        private const val SUM_KEY = "sum"

        fun create(account: Account, sum: String) = PaymentPageFragment().apply {
            arguments = bundleOf(
                ACCOUNT_KEY to account,
                SUM_KEY to sum
            )
        }
    }

    private val binding: FragmentPaymentPageBinding by viewBinding()
    private val router: Router by inject()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.setNavigationOnClickListener {
            router.exit()
        }

        val account = requireArguments().getParcelable<Account>(ACCOUNT_KEY) ?: return
        val sum = requireArguments().getString(SUM_KEY, "0.0")

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val urlString = request?.url?.toString() ?: ""
                view?.loadUrl(urlString)
                return false
            }
        }

        binding.webView.loadDataWithBaseURL(
            account.paymentSystemUrl,
            getHtmlForm(account.paymentSystemUrl, sum, account.id),
            MIME_TYPE,
            ENCODING,
            null
        )
    }

    private fun getHtmlForm(paymentSystemUrl: String, sum: String, accountId: String): String =
        """
            <!doctype html>
                <html>
                <head>
                    <style>
                        html {
                            height: 100%;
                            overflow: hidden;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            width: 100%;
                            height: 100%;
                            background: url(/pic/loading.gif) no-repeat center
                        }

                        div {
                            display: none;
                        }
                    </style>
                </head>
                <body>
                    <div>
                        <form method='POST' action='$paymentSystemUrl/create/'>
                            <input type='text' name='sum' value='$sum'/>
                            <input type='text' name='clientid' value='$accountId'/>
                            <input type='submit' value='pay'/>
                        </form>
                    </div>
                    <script>
                        document.forms[0].submit();
                    </script>
                </body>
                </html>
        """.trimIndent()

}