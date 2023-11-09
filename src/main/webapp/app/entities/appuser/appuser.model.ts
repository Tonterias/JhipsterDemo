import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IAppuser {
  id: number;
  date?: dayjs.Dayjs | null;
  insuranceCompany?: string | null;
  balance?: number | null;
  country?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewAppuser = Omit<IAppuser, 'id'> & { id: null };
