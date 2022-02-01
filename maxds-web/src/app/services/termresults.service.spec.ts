import { TestBed } from '@angular/core/testing';

import { TermresultsService } from '../services/termresults.service';

describe('TermresultsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TermresultsService = TestBed.get(TermresultsService);
    expect(service).toBeTruthy();
  });
});
