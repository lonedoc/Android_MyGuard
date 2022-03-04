package kobramob.rubeg38.ru.myprotection.feature.accounts.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.domain.models.Account

class AccountsAdapter(context: Context, accounts: MutableList<Account>) :
    ArrayAdapter<Account>(context, 0, accounts)
{

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val text = getItem(position)?.let { account -> getTextRepresentation(account) }

        val view = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        (view as? TextView)?.text = text

        return view
    }

    private fun getTextRepresentation(account: Account): String {
        val guardServicePart = if (account.guardServiceName == null)
            ""
        else
            " - ${account.guardServiceName}"

        return "${account.id}$guardServicePart"
    }

}