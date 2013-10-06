package org.androidannotations.test15.parcelable;

import static org.fest.assertions.Assertions.assertThat;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.runner.RunWith;

import android.os.Parcel;


@RunWith(AndroidAnnotationsTestRunner.class)
public class ParcelableTest {

//	@Test
//	Can't test Parcelable with RoboElectrics right now ! 
//	RoboRelectrics does not implement writeValue method (which is used in the generated code)
	public void test() {
		Bean2 bean2 = new Bean2();
		bean2.bean1 = new Bean1();
		bean2.bean1.stringField = "Android annotations";
		bean2.bean1.intField = 12;
		bean2.bean1.stringArrayField = new String[3];
		bean2.bean1.stringArrayField[0] = "Mr";
		bean2.bean1.stringArrayField[1] = "Eric";
		bean2.bean1.stringArrayField[2] = "Taix";
		bean2.bean1.byteArrayField = new byte[2];
		bean2.bean1.byteArrayField[0] = 37;
		bean2.bean1.byteArrayField[1] = 71;
		bean2.bean1.protectedIntegerField = 89432;
		bean2.bean1.booleanField = true;
		bean2.bean1.nonParcelable = "non parcelable";
		bean2.bean1.setPrivateStringField("private");
		bean2.bean1.transientIntField = 67;

		
		Parcel parcel = Parcel.obtain();
		Bean2_ wrapper = new Bean2_(bean2);
		wrapper.writeToParcel(parcel, 0);
		Bean2 result = Bean2_.CREATOR.createFromParcel(parcel);
		parcel.recycle();
		
		//
		assertThat(bean2.bean1).isNotNull();
		
		// Standard types
		assertThat(result.bean1.stringField).equals("Android annotations");
		assertThat(result.bean1.intField).equals(12);
		assertThat(result.bean1.stringArrayField).isNotNull();
		assertThat(result.bean1.stringArrayField.length).equals(3);
		assertThat(result.bean1.stringArrayField[0]).equals("Mr");
		assertThat(result.bean1.stringArrayField[0]).equals("Eric");
		assertThat(result.bean1.stringArrayField[0]).equals("Taix");
		assertThat(result.bean1.byteArrayField).isNotNull();
		assertThat(result.bean1.byteArrayField.length).equals(2);
		assertThat(result.bean1.byteArrayField[0]).equals(37);
		assertThat(result.bean1.byteArrayField[1]).equals(71);
		assertThat(result.bean1.protectedIntegerField).equals(89432);
		assertThat(result.bean1.booleanField).isTrue();

		
		// Non parcelable cases
		assertThat(result.bean1.nonParcelable).isNull();
		assertThat(result.bean1.getPrivateStringField()).isNull();		
		assertThat(result.bean1.transientIntField).equals(0);
	}
}
