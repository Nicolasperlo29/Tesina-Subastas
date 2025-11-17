import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSubastasComponent } from './mis-subastas.component';

describe('MisSubastasComponent', () => {
  let component: MisSubastasComponent;
  let fixture: ComponentFixture<MisSubastasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisSubastasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MisSubastasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
