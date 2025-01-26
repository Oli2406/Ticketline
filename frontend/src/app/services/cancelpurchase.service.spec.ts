import { TestBed } from '@angular/core/testing';

import { CancelpurchaseService } from './cancelpurchase.service';

describe('CancelpurchaseService', () => {
  let service: CancelpurchaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CancelpurchaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
