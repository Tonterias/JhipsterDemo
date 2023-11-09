import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AppuserFormService } from './appuser-form.service';
import { AppuserService } from '../service/appuser.service';
import { IAppuser } from '../appuser.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { AppuserUpdateComponent } from './appuser-update.component';

describe('Appuser Management Update Component', () => {
  let comp: AppuserUpdateComponent;
  let fixture: ComponentFixture<AppuserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appuserFormService: AppuserFormService;
  let appuserService: AppuserService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AppuserUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AppuserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppuserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appuserFormService = TestBed.inject(AppuserFormService);
    appuserService = TestBed.inject(AppuserService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const appuser: IAppuser = { id: 456 };
      const user: IUser = { id: 36109 };
      appuser.user = user;

      const userCollection: IUser[] = [{ id: 90291 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appuser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const appuser: IAppuser = { id: 456 };
      const user: IUser = { id: 62431 };
      appuser.user = user;

      activatedRoute.data = of({ appuser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.appuser).toEqual(appuser);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppuser>>();
      const appuser = { id: 123 };
      jest.spyOn(appuserFormService, 'getAppuser').mockReturnValue(appuser);
      jest.spyOn(appuserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appuser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appuser }));
      saveSubject.complete();

      // THEN
      expect(appuserFormService.getAppuser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(appuserService.update).toHaveBeenCalledWith(expect.objectContaining(appuser));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppuser>>();
      const appuser = { id: 123 };
      jest.spyOn(appuserFormService, 'getAppuser').mockReturnValue({ id: null });
      jest.spyOn(appuserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appuser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appuser }));
      saveSubject.complete();

      // THEN
      expect(appuserFormService.getAppuser).toHaveBeenCalled();
      expect(appuserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppuser>>();
      const appuser = { id: 123 };
      jest.spyOn(appuserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appuser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appuserService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
