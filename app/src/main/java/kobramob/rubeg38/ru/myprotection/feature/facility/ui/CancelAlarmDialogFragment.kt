package kobramob.rubeg38.ru.myprotection.feature.facility.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import kobramob.rubeg38.ru.myprotection.R
import kobramob.rubeg38.ru.myprotection.databinding.FragmentCancelAlarmDialogBinding

class CancelAlarmDialogFragment : DialogFragment() {

    companion object {
        private const val PASSCODES_KEY = "passcodes_key"
        const val REQUEST_KEY = "cancel_alarm_dialog_request_key"
        const val PASSCODE_KEY = "passcode_key"

        fun create(passcodes: List<String>) = CancelAlarmDialogFragment().apply {
            arguments = Bundle().apply { putStringArrayList(PASSCODES_KEY, ArrayList(passcodes)) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentCancelAlarmDialogBinding.inflate(layoutInflater)
        val passcodes = requireArguments().getStringArrayList(PASSCODES_KEY) ?: listOf()

        val dialog = AlertDialog.Builder(requireContext(),R.style.AlertDialogCustom).run {
            setTitle(requireContext().getString(R.string.cancel_alarm_dialog_title))
            setMessage(R.string.cancel_alarm_dialog_message)
            setView(binding.root)

            val positiveButtonTextRes = R.string.cancel_alarm_dialog_positive_button_text
            val negativeButtonTextRes = R.string.cancel_alarm_dialog_negative_button_text

            setPositiveButton(requireContext().getString(positiveButtonTextRes)) { _, _ ->
                val selectedPasscode = when (binding.root.checkedRadioButtonId) {
                    R.id.word1 -> passcodes[1]
                    R.id.word2 -> passcodes[2]
                    R.id.word3 -> passcodes[3]
                    else -> passcodes[0]
                }

                setFragmentResult(REQUEST_KEY, bundleOf(PASSCODE_KEY to selectedPasscode))
            }

            setNegativeButton(requireContext().getString(negativeButtonTextRes)) { _, _ -> }

            create()
        }

        binding.word0.text = passcodes[0]
        binding.word1.text = passcodes[1]
        binding.word2.text = passcodes[2]
        binding.word3.text = passcodes[3]

        binding.root.clearCheck()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { positiveButton ->
                positiveButton.isEnabled = false

            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(R.color.blue_500))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(R.color.red_500))

            binding.root.setOnCheckedChangeListener { _, checkedId ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { positiveButton ->
                    positiveButton.isEnabled = checkedId != -1
                }
            }
        }

        return dialog
    }



}