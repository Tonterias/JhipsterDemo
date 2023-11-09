import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../appuser.test-samples';

import { AppuserFormService } from './appuser-form.service';

describe('Appuser Form Service', () => {
  let service: AppuserFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppuserFormService);
  });

  describe('Service methods', () => {
    describe('createAppuserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAppuserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            insuranceCompany: expect.any(Object),
            balance: expect.any(Object),
            country: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IAppuser should create a new form with FormGroup', () => {
        const formGroup = service.createAppuserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            insuranceCompany: expect.any(Object),
            balance: expect.any(Object),
            country: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getAppuser', () => {
      it('should return NewAppuser for default Appuser initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAppuserFormGroup(sampleWithNewData);

        const appuser = service.getAppuser(formGroup) as any;

        expect(appuser).toMatchObject(sampleWithNewData);
      });

      it('should return NewAppuser for empty Appuser initial value', () => {
        const formGroup = service.createAppuserFormGroup();

        const appuser = service.getAppuser(formGroup) as any;

        expect(appuser).toMatchObject({});
      });

      it('should return IAppuser', () => {
        const formGroup = service.createAppuserFormGroup(sampleWithRequiredData);

        const appuser = service.getAppuser(formGroup) as any;

        expect(appuser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAppuser should not enable id FormControl', () => {
        const formGroup = service.createAppuserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAppuser should disable id FormControl', () => {
        const formGroup = service.createAppuserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
