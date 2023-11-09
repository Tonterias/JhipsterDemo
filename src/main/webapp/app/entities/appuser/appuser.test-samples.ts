import dayjs from 'dayjs/esm';

import { IAppuser, NewAppuser } from './appuser.model';

export const sampleWithRequiredData: IAppuser = {
  id: 77401,
  date: dayjs('2023-11-08T22:39'),
};

export const sampleWithPartialData: IAppuser = {
  id: 90931,
  date: dayjs('2023-11-09T00:28'),
  insuranceCompany: 'Human',
  balance: 10664,
};

export const sampleWithFullData: IAppuser = {
  id: 43951,
  date: dayjs('2023-11-09T06:32'),
  insuranceCompany: 'B2C Re-engineered',
  balance: 3441,
  country: 'Cambodia',
};

export const sampleWithNewData: NewAppuser = {
  date: dayjs('2023-11-09T12:56'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
