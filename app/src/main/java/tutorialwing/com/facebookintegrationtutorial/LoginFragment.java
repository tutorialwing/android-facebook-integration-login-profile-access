package tutorialwing.com.facebookintegrationtutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginFragment extends Fragment {

	private CallbackManager callbackManager;

	private TextView textView;

	private AccessTokenTracker accessTokenTracker;
	private ProfileTracker profileTracker;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize facebook SDK.
		FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

		// Create a callbackManager to handle the login responses.
		callbackManager = CallbackManager.Factory.create();

		accessTokenTracker= new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
				Toast.makeText(getActivity(), "AccessToken changed", Toast.LENGTH_SHORT).show();
			}
		};

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
				displayMessage(newProfile);
			}
		};

		accessTokenTracker.startTracking();
		profileTracker.startTracking();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		textView = (TextView) view.findViewById(R.id.textView);
		customizeLoinButton(view);
	}

	private void customizeLoinButton(View view) {
		LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
		loginButton.setReadPermissions("user_friends");
		loginButton.setFragment(this);
		loginButton.registerCallback(callbackManager, callback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			AccessToken accessToken = loginResult.getAccessToken();
			Toast.makeText(getActivity(), "AccessToken = " + accessToken, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getActivity(), "User Cancelled login", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(FacebookException e) {
			Toast.makeText(getActivity(), "Error occurred while login", Toast.LENGTH_SHORT).show();
		}
	};

	private void displayMessage(Profile profile){
			String name = (profile != null) ? profile.getName() : "User not logged in";
			textView.setText(name);
	}

	@Override
	public void onStop() {
		super.onStop();
		accessTokenTracker.stopTracking();
		profileTracker.stopTracking();
	}

	@Override
	public void onResume() {
		super.onResume();
		Profile profile = Profile.getCurrentProfile();
		displayMessage(profile);
	}
}