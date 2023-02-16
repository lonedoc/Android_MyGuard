package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentRenameDialogBinding
import kobramob.rubeg38.ru.myprotection.utils.setTextChangedListener

class RenameDialogFragment : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "rename_dialog_request_key"
        const val NAME_KEY = "name_key"

        fun create(currentName: String) = RenameDialogFragment().apply {
            arguments = bundleOf(NAME_KEY to currentName)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentRenameDialogBinding.inflate(layoutInflater)

        val currentName = requireArguments().getString(NAME_KEY) ?: ""
        binding.nameEditText.setText(currentName)

        val dialog = AlertDialog.Builder(requireContext()).run {
            setTitle(R.string.rename_dialog_title)
            setView(binding.root)

            setPositiveButton(R.string.rename_dialog_positive_button_text) { _, _ ->
                val name = binding.nameEditText.text.toString()
                setFragmentResult(REQUEST_KEY, bundleOf(NAME_KEY to name))
            }

            setNegativeButton(R.string.rename_dialog_negative_button_text) { _, _ -> }
            create()
        }

        binding.nameEditText.setTextChangedListener { text ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { positiveButton ->
                positiveButton.isEnabled = text.isNotBlank()
            }
        }

        return dialog
    }

}