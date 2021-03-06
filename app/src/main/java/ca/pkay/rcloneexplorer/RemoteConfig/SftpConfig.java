package ca.pkay.rcloneexplorer.RemoteConfig;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import ca.pkay.rcloneexplorer.R;
import ca.pkay.rcloneexplorer.Rclone;
import es.dmoral.toasty.Toasty;

public class SftpConfig extends Fragment {

    private Context context;
    private Rclone rclone;
    private TextInputLayout remoteNameInputLayout;
    private TextInputLayout hostInputLayout;
    private TextInputLayout userInputLayout;
    private TextInputLayout portInputLayout;
    private TextInputLayout passInputLayout;
    private EditText remoteName;
    private EditText host;
    private EditText user;
    private EditText port;
    private EditText pass;

    public SftpConfig() {}

    public static SftpConfig newInstance() { return new SftpConfig(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() == null) {
            return;
        }
        context = getContext();
        rclone = new Rclone(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remote_config_form, container, false);
        setUpForm(view);
        return view;
    }

    private void setUpForm(View view) {
        ViewGroup formContent = view.findViewById(R.id.form_content);
        int padding = getResources().getDimensionPixelOffset(R.dimen.config_form_template);
        remoteNameInputLayout = view.findViewById(R.id.remote_name_layout);
        remoteNameInputLayout.setVisibility(View.VISIBLE);
        remoteName = view.findViewById(R.id.remote_name);

        View hostInputTemplate = View.inflate(context, R.layout.config_form_template_edit_text, null);
        hostInputTemplate.setPadding(0, 0, 0, padding);
        formContent.addView(hostInputTemplate);
        hostInputLayout = hostInputTemplate.findViewById(R.id.text_input_layout);
        hostInputLayout.setHint(getString(R.string.sftp_host_hint));
        host = hostInputTemplate.findViewById(R.id.edit_text);

        View userInputLayoutTemplate = View.inflate(context, R.layout.config_form_template_edit_text, null);
        userInputLayoutTemplate.setPadding(0, 0, 0, padding);
        formContent.addView(userInputLayoutTemplate);
        userInputLayout = userInputLayoutTemplate.findViewById(R.id.text_input_layout);
        userInputLayout.setHint(getString(R.string.ssh_user_hint));
        user = userInputLayoutTemplate.findViewById(R.id.edit_text);

        View passInputTemplate = View.inflate(context, R.layout.config_form_template_password, null);
        passInputTemplate.setPadding(0, 0, 0, padding);
        formContent.addView(passInputTemplate);
        passInputLayout = passInputTemplate.findViewById(R.id.pass_input_layout);
        passInputLayout.setHint(getString(R.string.ssh_pass_hint));
        pass = passInputTemplate.findViewById(R.id.pass);

        View portInputTemplate = View.inflate(context, R.layout.config_form_template_edit_text, null);
        portInputTemplate.setPadding(0, 0, 0, padding);
        formContent.addView(portInputTemplate);
        portInputLayout = portInputTemplate.findViewById(R.id.text_input_layout);
        portInputLayout.setHint(getString(R.string.ssh_port_hint));
        port = portInputTemplate.findViewById(R.id.edit_text);
        port.setInputType(InputType.TYPE_CLASS_NUMBER);
        port.setText(R.string.sftp_default_port);

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpRemote();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    private void setUpRemote() {
        String name = remoteName.getText().toString();
        String hostString = host.getText().toString();
        String userString = user.getText().toString();
        String portString = port.getText().toString();
        String passString = pass.getText().toString();
        boolean error = false;

        if (name.trim().isEmpty()) {
            remoteNameInputLayout.setErrorEnabled(true);
            remoteNameInputLayout.setError(getString(R.string.remote_name_cannot_be_empty));
            error = true;
        } else {
            remoteNameInputLayout.setErrorEnabled(false);
        }
        if (hostString.trim().isEmpty()) {
            hostInputLayout.setErrorEnabled(true);
            hostInputLayout.setError(getString(R.string.required_field));
            error = true;
        } else {
            hostInputLayout.setErrorEnabled(false);
        }
        if (userString.trim().isEmpty()) {
            userInputLayout.setErrorEnabled(true);
            userInputLayout.setError(getString(R.string.required_field));
            error = true;
        } else {
            userInputLayout.setErrorEnabled(false);
        }
        if (portString.trim().isEmpty()) {
            portInputLayout.setErrorEnabled(true);
            portInputLayout.setError(getString(R.string.required_field));
            error = true;
        } else {
            portInputLayout.setErrorEnabled(false);
        }
        if (passString.trim().isEmpty()) {
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError(getString(R.string.required_field));
            error = true;
        } else {
            passInputLayout.setErrorEnabled(false);
        }
        if (error) {
            return;
        }

        ArrayList<String> options = new ArrayList<>();
        options.add(name);
        options.add("sftp");
        options.add("host");
        options.add(hostString);
        options.add("user");
        options.add(userString);
        options.add("port");
        options.add(portString);

        String obscuredPass = rclone.obscure(passString);
        options.add("pass");
        options.add(obscuredPass);

        RemoteConfigHelper.setupAndWait(context, options);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
