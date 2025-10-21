import { TestBed } from '@angular/core/testing';

import { Beneficio } from './beneficio-service';

describe('Beneficio', () => {
  let service: Beneficio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Beneficio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
