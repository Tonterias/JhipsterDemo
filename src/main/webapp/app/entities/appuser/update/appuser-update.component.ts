import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AppuserFormService, AppuserFormGroup } from './appuser-form.service';
import { IAppuser } from '../appuser.model';
import { AppuserService } from '../service/appuser.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-appuser-update',
  templateUrl: './appuser-update.component.html',
})
export class AppuserUpdateComponent implements OnInit {
  isSaving = false;
  appuser: IAppuser | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: AppuserFormGroup = this.appuserFormService.createAppuserFormGroup();

  constructor(
    protected appuserService: AppuserService,
    protected appuserFormService: AppuserFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appuser }) => {
      this.appuser = appuser;
      if (appuser) {
        this.updateForm(appuser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appuser = this.appuserFormService.getAppuser(this.editForm);
    if (appuser.id !== null) {
      this.subscribeToSaveResponse(this.appuserService.update(appuser));
    } else {
      this.subscribeToSaveResponse(this.appuserService.create(appuser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppuser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(appuser: IAppuser): void {
    this.appuser = appuser;
    this.appuserFormService.resetForm(this.editForm, appuser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, appuser.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.appuser?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
