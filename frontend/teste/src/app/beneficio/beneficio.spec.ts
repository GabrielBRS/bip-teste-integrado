import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Beneficio } from './beneficio';

describe('Beneficio', () => {
  let component: Beneficio;
  let fixture: ComponentFixture<Beneficio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Beneficio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Beneficio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
