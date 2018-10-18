package dev.olog.msc.dagger.base;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import androidx.fragment.app.Fragment;
import dagger.MapKey;

import static java.lang.annotation.ElementType.METHOD;

@MapKey
@Documented
@Target(METHOD)
public @interface FragmentXKey {
    Class<? extends Fragment> value();
}
