import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppuser, NewAppuser } from '../appuser.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppuser for edit and NewAppuserFormGroupInput for create.
 */
type AppuserFormGroupInput = IAppuser | PartialWithRequiredKeyOf<NewAppuser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppuser | NewAppuser> = Omit<T, 'date'> & {
  date?: string | null;
};

type AppuserFormRawValue = FormValueOf<IAppuser>;

type NewAppuserFormRawValue = FormValueOf<NewAppuser>;

type AppuserFormDefaults = Pick<NewAppuser, 'id' | 'date'>;

type AppuserFormGroupContent = {
  id: FormControl<AppuserFormRawValue['id'] | NewAppuser['id']>;
  date: FormControl<AppuserFormRawValue['date']>;
  insuranceCompany: FormControl<AppuserFormRawValue['insuranceCompany']>;
  balance: FormControl<AppuserFormRawValue['balance']>;
  country: FormControl<AppuserFormRawValue['country']>;
  user: FormControl<AppuserFormRawValue['user']>;
};

export type AppuserFormGroup = FormGroup<AppuserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppuserFormService {
  createAppuserFormGroup(appuser: AppuserFormGroupInput = { id: null }): AppuserFormGroup {
    const appuserRawValue = this.convertAppuserToAppuserRawValue({
      ...this.getFormDefaults(),
      ...appuser,
    });
    return new FormGroup<AppuserFormGroupContent>({
      id: new FormControl(
        { value: appuserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(appuserRawValue.date, {
        validators: [Validators.required],
      }),
      insuranceCompany: new FormControl(appuserRawValue.insuranceCompany),
      balance: new FormControl(appuserRawValue.balance),
      country: new FormControl(appuserRawValue.country),
      user: new FormControl(appuserRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getAppuser(form: AppuserFormGroup): IAppuser | NewAppuser {
    return this.convertAppuserRawValueToAppuser(form.getRawValue() as AppuserFormRawValue | NewAppuserFormRawValue);
  }

  resetForm(form: AppuserFormGroup, appuser: AppuserFormGroupInput): void {
    const appuserRawValue = this.convertAppuserToAppuserRawValue({ ...this.getFormDefaults(), ...appuser });
    form.reset(
      {
        ...appuserRawValue,
        id: { value: appuserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AppuserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertAppuserRawValueToAppuser(rawAppuser: AppuserFormRawValue | NewAppuserFormRawValue): IAppuser | NewAppuser {
    return {
      ...rawAppuser,
      date: dayjs(rawAppuser.date, DATE_TIME_FORMAT),
    };
  }

  private convertAppuserToAppuserRawValue(
    appuser: IAppuser | (Partial<NewAppuser> & AppuserFormDefaults)
  ): AppuserFormRawValue | PartialWithRequiredKeyOf<NewAppuserFormRawValue> {
    return {
      ...appuser,
      date: appuser.date ? appuser.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
