import { Component } from '@angular/core';
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { MerchandiseService } from "../../../services/merchandise.service";
import { ToastrService } from "ngx-toastr";
import { Router } from "@angular/router";

@Component({
  selector: 'app-merchandise-create',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
  ],
  templateUrl: './merchandise-create.component.html',
  styleUrls: ['./merchandise-create.component.scss']
})
export class MerchandiseCreateComponent {
  merchandiseData = {
    name: '',
    category: '',
    stock: 0,
    price: null,
  };
  imageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private merchandiseService: MerchandiseService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.imageFile = file;

      this.imagePreview = URL.createObjectURL(file);

      console.log('Selected file:', file.name, file.size, file.type);
    } else {
      this.imageFile = null;
      this.imagePreview = null;
    }
  }

  onSubmit(): void {
    if (!this.merchandiseData.name || !this.merchandiseData.category || this.merchandiseData.stock <= 0) {
      this.toastr.error("Please fill all required fields.", "Validation Error");
      return;
    }

    this.merchandiseService.createMerchandise(this.merchandiseData, this.imageFile).subscribe(
      (response) => {
        this.toastr.success("Merchandise created successfully!", "Success");
        this.resetForm();
        this.router.navigate(['/admin']);
      },
      (error) => {
        console.error('Error occurred:', error);
        this.toastr.error("Failed to create merchandise. Please try again.", "Error");
      }
    );
  }

  resetForm(): void {
    this.merchandiseData = {
      name: '',
      category: '',
      stock: 0,
      price: null,
    };
    this.imageFile = null;
    this.imagePreview = null;
  }
}
