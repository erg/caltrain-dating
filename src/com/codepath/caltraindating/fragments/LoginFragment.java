package com.codepath.caltraindating.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.caltraindating.R;
import com.codepath.caltraindating.models.MultiAsyncExecutor;
import com.codepath.caltraindating.models.MultiAsyncExecutor.Task;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginFragment extends Fragment implements OnClickListener {

	Listener listener = null;
	static final int FB_REQUEST_COUNT = 2;
	int fbRequestsComplete = 0;

	public interface Listener {
		public void setUser(ParseUser u);

		public ParseUser getUser();

		public String getBlurb();
		
		public void fbDataUpdated();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_login, container, false);
		Button login = (Button) v.findViewById(R.id.btLogin);
		login.setOnClickListener(this);

		return v;

	}

	public void fbLogin() {
		String[] params = { "user_birthday", "user_photos" };

		ParseFacebookUtils.logIn(Arrays.asList(params), getActivity(),
				new LogInCallback() {
					@Override
					public void done(ParseUser user,
							com.parse.ParseException err) {
						listener.setUser(user);
						if (user == null) {
							Log.d("tag",
									"Uh oh. The user cancelled the Facebook login. "
											+ err.getMessage());
							return;
						}
						getFacebookDataInBackground(true);
					}
				});
	}

	private void fbDataCheck() {
		fbRequestsComplete++;
		if (fbRequestsComplete == FB_REQUEST_COUNT) {
			listener.fbDataUpdated();
		}
	}

	public void getFacebookDataInBackground(final boolean notifyListener) {
		fbRequestsComplete = 0;
		final Bundle bundle = new Bundle();
		String query = "SELECT src_big,src FROM photo WHERE aid IN (SELECT aid FROM album WHERE owner=me() AND name='Profile Pictures')";
		bundle.putString("q", query);

		final MultiAsyncExecutor fbEx = new MultiAsyncExecutor(
				new MultiAsyncExecutor.OnAllComplete() {
					@Override
					public void complete() {
						Log.e("tag", "fb tasks all done");
						if (notifyListener) {
							listener.fbDataUpdated();
						}
					}
				});
		fbEx.addTask(fbEx.new Task() {
			@Override
			public void start() {
				Request photoRequest = new Request(ParseFacebookUtils
						.getSession(), "/fql", bundle, HttpMethod.GET,
						new Request.Callback() {

							@Override
							public void onCompleted(Response response) {
								GraphObject graphObject = response
										.getGraphObject();
								if (graphObject != null) {
									try {
										JSONArray photos = graphObject
												.getInnerJSONObject()
												.getJSONArray("data");
										final ArrayList<String> srcs = new ArrayList<String>();
										final ArrayList<String> big_srcs = new ArrayList<String>();
										for (int i = 0; i < photos.length(); i++) {
											JSONObject obj = photos
													.getJSONObject(i);
											Log.d("DEBUG",
													"src:"
															+ obj.getString("src"));
											srcs.add(obj.getString("src"));
											big_srcs.add(obj
													.getString("src_big"));
											if (i > 9) {
												break;
											}
										}
										final ParseUser currentUser = listener
												.getUser();
										MultiAsyncExecutor mEx = new MultiAsyncExecutor(
												new MultiAsyncExecutor.OnAllComplete() {
													@Override
													public void complete() {
														fbEx.completeItem();
													}
												});
										mEx.addTask(buildPhotoTask(mEx,
												"imgSrcs", srcs, currentUser));
										mEx.addTask(buildPhotoTask(mEx,
												"imgBigSrcs", big_srcs,
												currentUser));
										mEx.executeAll();
									} catch (JSONException e) {
										e.printStackTrace();
										Log.e("tag", "json exception");
									}
								}
							}
						});
				photoRequest.executeAsync();
			}
		});

		fbEx.addTask(fbEx.new Task() {
			@Override
			public void start() {
				Request.newMeRequest(ParseFacebookUtils.getSession(),
						new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user,
									Response response) {
								if (user != null) {
									ParseUser currentUser = listener.getUser();
									currentUser.put("fbId", user.getId());
									currentUser.put("firstName",
											user.getFirstName());
									currentUser.put("lastName",
											user.getLastName());
									currentUser.put("birthday",
											user.getBirthday());
									currentUser
											.saveInBackground(new SaveCallback() {
												@Override
												public void done(
														ParseException arg0) {
													complete();
												}
											});
								} else {
									Log.e("tag", "error getting facebook data");
								}
							}
						}).executeAsync();
			}
		});
		fbEx.executeAll();

	}

	private Task buildPhotoTask(MultiAsyncExecutor mEx, final String key,
			final ArrayList<String> srcs, final ParseUser currentUser) {
		return mEx.new Task() {
			@Override
			public void start() {
				List<String> photoList = currentUser.getList(key);
				if(photoList != null && photoList.size()>0){
					currentUser.removeAll(key, photoList);
				}
				currentUser.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						currentUser.addAllUnique(key, srcs);
						currentUser.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException arg0) {
								complete();
							}
						});
					}
				});
			}
		};
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		fbLogin();
	}
}
