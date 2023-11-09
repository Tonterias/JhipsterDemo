import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppuser } from '../appuser.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../appuser.test-samples';

import { AppuserService, RestAppuser } from './appuser.service';

const requireRestSample: RestAppuser = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('Appuser Service', () => {
  let service: AppuserService;
  let httpMock: HttpTestingController;
  let expectedResult: IAppuser | IAppuser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AppuserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Appuser', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const appuser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(appuser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Appuser', () => {
      const appuser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(appuser).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Appuser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Appuser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Appuser', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAppuserToCollectionIfMissing', () => {
      it('should add a Appuser to an empty array', () => {
        const appuser: IAppuser = sampleWithRequiredData;
        expectedResult = service.addAppuserToCollectionIfMissing([], appuser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appuser);
      });

      it('should not add a Appuser to an array that contains it', () => {
        const appuser: IAppuser = sampleWithRequiredData;
        const appuserCollection: IAppuser[] = [
          {
            ...appuser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAppuserToCollectionIfMissing(appuserCollection, appuser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Appuser to an array that doesn't contain it", () => {
        const appuser: IAppuser = sampleWithRequiredData;
        const appuserCollection: IAppuser[] = [sampleWithPartialData];
        expectedResult = service.addAppuserToCollectionIfMissing(appuserCollection, appuser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appuser);
      });

      it('should add only unique Appuser to an array', () => {
        const appuserArray: IAppuser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const appuserCollection: IAppuser[] = [sampleWithRequiredData];
        expectedResult = service.addAppuserToCollectionIfMissing(appuserCollection, ...appuserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appuser: IAppuser = sampleWithRequiredData;
        const appuser2: IAppuser = sampleWithPartialData;
        expectedResult = service.addAppuserToCollectionIfMissing([], appuser, appuser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appuser);
        expect(expectedResult).toContain(appuser2);
      });

      it('should accept null and undefined values', () => {
        const appuser: IAppuser = sampleWithRequiredData;
        expectedResult = service.addAppuserToCollectionIfMissing([], null, appuser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appuser);
      });

      it('should return initial array if no Appuser is added', () => {
        const appuserCollection: IAppuser[] = [sampleWithRequiredData];
        expectedResult = service.addAppuserToCollectionIfMissing(appuserCollection, undefined, null);
        expect(expectedResult).toEqual(appuserCollection);
      });
    });

    describe('compareAppuser', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAppuser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAppuser(entity1, entity2);
        const compareResult2 = service.compareAppuser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAppuser(entity1, entity2);
        const compareResult2 = service.compareAppuser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAppuser(entity1, entity2);
        const compareResult2 = service.compareAppuser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
