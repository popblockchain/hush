/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.stealth.service;

import android.app.Activity;
import android.os.Bundle;

import com.stealth.common.activities.FragmentActivityBase;
import com.stealth.hushkbd.R;
import com.stealth.util.Util;

/**
 * A simple launcher activity containing a summary stealth description, stealth log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the stealth log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 - (void)addScrollViewImage {
 for (NSInteger index = 0; index < IMAGE_NUM; index++) {
 UIImageView *imageView = [[UIImageView alloc] init];
 CGFloat imageViewX = index * (self.scrollView.frame.size.width);
 imageView.frame = CGRectMake(imageViewX, 0, self.scrollView.frame.size.width , self.scrollView.frame.size.height);
 imageView.image = [UIImage imageNamed:@"搜索图片"];
 imageView.contentMode =  UIViewContentModeScaleAspectFill;
 [self.scrollView addSubview:imageView];
 imageView.contentMode =  UIViewContentModeScaleToFill;
 }
 self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width * IMAGE_NUM, self.scrollView.frame.size.height);
 }
 */
public class Help extends Activity {

    public static final String TAG = "Help";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.isKoreanLanguage())
            setContentView(R.layout.help_korean);
        else
            setContentView(R.layout.help_english);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
    /** Create a chain of targets that will receive log data */
}
