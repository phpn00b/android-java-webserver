/*
 *
 *  * Copyright (C) 2015. Matt Van Horn (http://www.musingsofacodefiend.com/)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.foxhorn.foxyserver.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
	private ToggleButton tbActivateServer;
	private boolean isWorking;
	private boolean isRunning;
	private final Bootstrap bootstrap = new Bootstrap();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tbActivateServer = (ToggleButton) findViewById(R.id.tbServerOn);
		tbActivateServer.setOnCheckedChangeListener(this);
	}

	/**
	 * Called when the checked state of a compound button has changed.
	 *
	 * @param buttonView The compound button view whose state has changed.
	 * @param isChecked  The new checked state of buttonView.
	 */
	@Override
	public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
		if (isWorking)
			return;
		if (isRunning == isChecked)
			return;
		isWorking = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (isChecked)
						bootstrap.start(MainActivity.this.getApplicationContext());
					else
						bootstrap.stop();
					isRunning = isChecked;
				} catch (Exception e) {
					e.printStackTrace();
				}
				enableToggleButtonAndSetCorrectState();
			}
		}).start();
	}

	private void enableToggleButtonAndSetCorrectState() {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					enableToggleButtonAndSetCorrectState();
				}
			});
			return;
		}
		tbActivateServer.setChecked(isRunning);
		tbActivateServer.setEnabled(true);
		isWorking = false;
	}
}
