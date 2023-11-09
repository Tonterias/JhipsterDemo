import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppuser, NewAppuser } from '../appuser.model';

export type PartialUpdateAppuser = Partial<IAppuser> & Pick<IAppuser, 'id'>;

type RestOf<T extends IAppuser | NewAppuser> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestAppuser = RestOf<IAppuser>;

export type NewRestAppuser = RestOf<NewAppuser>;

export type PartialUpdateRestAppuser = RestOf<PartialUpdateAppuser>;

export type EntityResponseType = HttpResponse<IAppuser>;
export type EntityArrayResponseType = HttpResponse<IAppuser[]>;

@Injectable({ providedIn: 'root' })
export class AppuserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/appusers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appuser: NewAppuser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appuser);
    return this.http
      .post<RestAppuser>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(appuser: IAppuser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appuser);
    return this.http
      .put<RestAppuser>(`${this.resourceUrl}/${this.getAppuserIdentifier(appuser)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(appuser: PartialUpdateAppuser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appuser);
    return this.http
      .patch<RestAppuser>(`${this.resourceUrl}/${this.getAppuserIdentifier(appuser)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAppuser>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAppuser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAppuserIdentifier(appuser: Pick<IAppuser, 'id'>): number {
    return appuser.id;
  }

  compareAppuser(o1: Pick<IAppuser, 'id'> | null, o2: Pick<IAppuser, 'id'> | null): boolean {
    return o1 && o2 ? this.getAppuserIdentifier(o1) === this.getAppuserIdentifier(o2) : o1 === o2;
  }

  addAppuserToCollectionIfMissing<Type extends Pick<IAppuser, 'id'>>(
    appuserCollection: Type[],
    ...appusersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const appusers: Type[] = appusersToCheck.filter(isPresent);
    if (appusers.length > 0) {
      const appuserCollectionIdentifiers = appuserCollection.map(appuserItem => this.getAppuserIdentifier(appuserItem)!);
      const appusersToAdd = appusers.filter(appuserItem => {
        const appuserIdentifier = this.getAppuserIdentifier(appuserItem);
        if (appuserCollectionIdentifiers.includes(appuserIdentifier)) {
          return false;
        }
        appuserCollectionIdentifiers.push(appuserIdentifier);
        return true;
      });
      return [...appusersToAdd, ...appuserCollection];
    }
    return appuserCollection;
  }

  protected convertDateFromClient<T extends IAppuser | NewAppuser | PartialUpdateAppuser>(appuser: T): RestOf<T> {
    return {
      ...appuser,
      date: appuser.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAppuser: RestAppuser): IAppuser {
    return {
      ...restAppuser,
      date: restAppuser.date ? dayjs(restAppuser.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAppuser>): HttpResponse<IAppuser> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAppuser[]>): HttpResponse<IAppuser[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
