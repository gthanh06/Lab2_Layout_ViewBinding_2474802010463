package vn.edu.vlu.lab2

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import vn.edu.vlu.lab2.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // (1) tạo cây View
        setContentView(binding.root)                           // (2) gắn lên màn hình
        setupUI()                                              // (3) dùng View an toàn
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener { handleLogin() }

        // Bấm "Done" trên bàn phím cũng đăng nhập luôn
        binding.edtPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleLogin()
                true
            } else {
                false
            }
        }

        // Gõ lại thì xóa thông báo lỗi cũ
        binding.edtEmail.doAfterTextChanged { binding.tilEmail.error = null }
        binding.edtPassword.doAfterTextChanged { binding.tilPassword.error = null }

        // Cấp 1: "Quên mật khẩu?" → Toast
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, R.string.msg_forgot_password, Toast.LENGTH_SHORT).show()
        }

        // Cấp 2: CheckBox "Hiện mật khẩu"
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPassword.transformationMethod =
                if (isChecked) null // hiện ký tự
                else PasswordTransformationMethod.getInstance()
            // giữ con trỏ ở cuối chuỗi
            binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)
        }
    }

    private fun handleLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        // Xóa thông báo lỗi của lần bấm trước
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        when {
            email.isEmpty() || password.isEmpty() -> {
                Toast.makeText(this, R.string.msg_missing_info, Toast.LENGTH_SHORT).show()
                binding.tvStatus.setText(R.string.status_missing)
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = getString(R.string.err_email_invalid)
                binding.tvStatus.text = ""
            }

            password.length < 6 -> {
                binding.tilPassword.error = getString(R.string.err_password_short)
                binding.tvStatus.text = ""
            }

            else -> {
                // Xác thực GIẢ LẬP: chưa gọi máy chủ. Xác thực thật ở các buổi sau.
                binding.tvStatus.text = getString(R.string.status_login_ok, email)

                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra(ProfileActivity.EXTRA_EMAIL, email)
                startActivity(intent)
            }
        }
    }
}
