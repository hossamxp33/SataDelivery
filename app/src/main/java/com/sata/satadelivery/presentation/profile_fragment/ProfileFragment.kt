package com.sata.satadelivery.presentation.profile_fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.sata.satadelivery.R
import com.sata.satadelivery.databinding.ProfileFragmentBinding
import com.sata.satadelivery.helper.*
import com.sata.satadelivery.presentation.map_activity.MapActivity
import com.sata.satadelivery.presentation.profile_fragment.viewmodel.ProfileViewmodel
import kotlinx.android.synthetic.main.delivery_login_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

import javax.inject.Inject

import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker


class ProfileFragment @Inject constructor() : Fragment() {

    companion object {
        const val TAG = "TownBottomSheetDialogFragment"
    }

    private val LOAD_IMG_REQUEST_CODE = 123
    private val REQUEST_PICK_PHOTO = 2
    private val REQUEST_TAKE_PHOTO = 0
    private var mediaPath: String? = null
    private var postPath: String? = null
    private val CAMERA_PIC_REQUEST = 1111
    private val mImageFileLocation = ""
    var fileUri: Uri? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ProfileViewmodel> { viewModelFactory }


    @Inject
    lateinit var pref: PreferenceHelper

    lateinit var view: ProfileFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.appComponent.inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        view = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment, container, false
        )

        view.listener = ClickHandler()
        view.context = context as MapActivity
        view.pref = (context as MapActivity).Pref


        view.appCompatImageView2.setOnClickListener {
            addimage()
        }
        view.editImage.setOnClickListener {
            addimage()
        }

        viewModel.getDeliversStatus(pref.delivery_status)
        try {
            viewModel.deliveryItemLD!!.observe(requireActivity()) {
                if (!it.isNullOrEmpty())
                    view.data = it[0]
                else
                    Error_MotionToast("لا يوجد بيانات ", requireActivity())
            }
        } catch (e: Exception) {
        }

        view.editBtn.setOnClickListener {
            editRequest()
            SUCCESS_MotionToast("تم التعديل", requireActivity())
        }

        return view.root
    }

    fun addimage() {

        ImagePicker.with(this)
            .compress(1024)         //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)
            .crop()    //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }


    private fun prepareFilePart(name: String, fileUri: Uri): MultipartBody.Part {
        var file: File? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            file = FileUtils.getFile(requireContext(), fileUri)
        }
        val requestFile = RequestBody.create(
            MediaType.parse("image"),
            file!!
        )


        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                fileUri = data?.data!!

                //  mProfileUri = fileUri
                view.appCompatImageView2.setImageURI(fileUri)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    fun editRequest() {
        val name = view.deliveryName.text.toString().replace("\"", "");
        val mobile = view.mobileNumber.text.toString().replace("\"", "");
        if (fileUri != null) {
            val photo_part = prepareFilePart("img", fileUri!!)
            viewModel.editDeliveryData(pref.deliveryId, photo_part, name, mobile)
        }

    }


}